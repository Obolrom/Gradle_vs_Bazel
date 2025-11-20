package com.romix.feature.feat494

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat494Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat494UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat494FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat494UserSummary
)

data class Feat494UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat494NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat494Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat494Config = Feat494Config()
) {

    fun loadSnapshot(userId: Long): Feat494NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat494NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat494UserSummary {
        return Feat494UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat494FeedItem> {
        val result = java.util.ArrayList<Feat494FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat494FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat494UiMapper {

    fun mapToUi(model: List<Feat494FeedItem>): Feat494UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat494UiModel(
            header = UiText("Feat494 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat494UiModel =
        Feat494UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat494UiModel =
        Feat494UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat494UiModel =
        Feat494UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat494Service(
    private val repository: Feat494Repository,
    private val uiMapper: Feat494UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat494UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat494UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat494UserItem1(val user: CoreUser, val label: String)
data class Feat494UserItem2(val user: CoreUser, val label: String)
data class Feat494UserItem3(val user: CoreUser, val label: String)
data class Feat494UserItem4(val user: CoreUser, val label: String)
data class Feat494UserItem5(val user: CoreUser, val label: String)
data class Feat494UserItem6(val user: CoreUser, val label: String)
data class Feat494UserItem7(val user: CoreUser, val label: String)
data class Feat494UserItem8(val user: CoreUser, val label: String)
data class Feat494UserItem9(val user: CoreUser, val label: String)
data class Feat494UserItem10(val user: CoreUser, val label: String)

data class Feat494StateBlock1(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock2(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock3(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock4(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock5(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock6(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock7(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock8(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock9(val state: Feat494UiModel, val checksum: Int)
data class Feat494StateBlock10(val state: Feat494UiModel, val checksum: Int)

fun buildFeat494UserItem(user: CoreUser, index: Int): Feat494UserItem1 {
    return Feat494UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat494StateBlock(model: Feat494UiModel): Feat494StateBlock1 {
    return Feat494StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat494UserSummary> {
    val list = java.util.ArrayList<Feat494UserSummary>(users.size)
    for (user in users) {
        list += Feat494UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat494UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat494UiModel {
    val summaries = (0 until count).map {
        Feat494UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat494UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat494UiModel> {
    val models = java.util.ArrayList<Feat494UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat494AnalyticsEvent1(val name: String, val value: String)
data class Feat494AnalyticsEvent2(val name: String, val value: String)
data class Feat494AnalyticsEvent3(val name: String, val value: String)
data class Feat494AnalyticsEvent4(val name: String, val value: String)
data class Feat494AnalyticsEvent5(val name: String, val value: String)
data class Feat494AnalyticsEvent6(val name: String, val value: String)
data class Feat494AnalyticsEvent7(val name: String, val value: String)
data class Feat494AnalyticsEvent8(val name: String, val value: String)
data class Feat494AnalyticsEvent9(val name: String, val value: String)
data class Feat494AnalyticsEvent10(val name: String, val value: String)

fun logFeat494Event1(event: Feat494AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat494Event2(event: Feat494AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat494Event3(event: Feat494AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat494Event4(event: Feat494AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat494Event5(event: Feat494AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat494Event6(event: Feat494AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat494Event7(event: Feat494AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat494Event8(event: Feat494AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat494Event9(event: Feat494AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat494Event10(event: Feat494AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat494Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat494Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat494(u: CoreUser): Feat494Projection1 =
    Feat494Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat494Projection1> {
    val list = java.util.ArrayList<Feat494Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat494(u)
    }
    return list
}
