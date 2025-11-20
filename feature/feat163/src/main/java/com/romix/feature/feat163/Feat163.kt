package com.romix.feature.feat163

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat163Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat163UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat163FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat163UserSummary
)

data class Feat163UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat163NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat163Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat163Config = Feat163Config()
) {

    fun loadSnapshot(userId: Long): Feat163NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat163NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat163UserSummary {
        return Feat163UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat163FeedItem> {
        val result = java.util.ArrayList<Feat163FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat163FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat163UiMapper {

    fun mapToUi(model: List<Feat163FeedItem>): Feat163UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat163UiModel(
            header = UiText("Feat163 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat163UiModel =
        Feat163UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat163UiModel =
        Feat163UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat163UiModel =
        Feat163UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat163Service(
    private val repository: Feat163Repository,
    private val uiMapper: Feat163UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat163UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat163UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat163UserItem1(val user: CoreUser, val label: String)
data class Feat163UserItem2(val user: CoreUser, val label: String)
data class Feat163UserItem3(val user: CoreUser, val label: String)
data class Feat163UserItem4(val user: CoreUser, val label: String)
data class Feat163UserItem5(val user: CoreUser, val label: String)
data class Feat163UserItem6(val user: CoreUser, val label: String)
data class Feat163UserItem7(val user: CoreUser, val label: String)
data class Feat163UserItem8(val user: CoreUser, val label: String)
data class Feat163UserItem9(val user: CoreUser, val label: String)
data class Feat163UserItem10(val user: CoreUser, val label: String)

data class Feat163StateBlock1(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock2(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock3(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock4(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock5(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock6(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock7(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock8(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock9(val state: Feat163UiModel, val checksum: Int)
data class Feat163StateBlock10(val state: Feat163UiModel, val checksum: Int)

fun buildFeat163UserItem(user: CoreUser, index: Int): Feat163UserItem1 {
    return Feat163UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat163StateBlock(model: Feat163UiModel): Feat163StateBlock1 {
    return Feat163StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat163UserSummary> {
    val list = java.util.ArrayList<Feat163UserSummary>(users.size)
    for (user in users) {
        list += Feat163UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat163UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat163UiModel {
    val summaries = (0 until count).map {
        Feat163UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat163UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat163UiModel> {
    val models = java.util.ArrayList<Feat163UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat163AnalyticsEvent1(val name: String, val value: String)
data class Feat163AnalyticsEvent2(val name: String, val value: String)
data class Feat163AnalyticsEvent3(val name: String, val value: String)
data class Feat163AnalyticsEvent4(val name: String, val value: String)
data class Feat163AnalyticsEvent5(val name: String, val value: String)
data class Feat163AnalyticsEvent6(val name: String, val value: String)
data class Feat163AnalyticsEvent7(val name: String, val value: String)
data class Feat163AnalyticsEvent8(val name: String, val value: String)
data class Feat163AnalyticsEvent9(val name: String, val value: String)
data class Feat163AnalyticsEvent10(val name: String, val value: String)

fun logFeat163Event1(event: Feat163AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat163Event2(event: Feat163AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat163Event3(event: Feat163AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat163Event4(event: Feat163AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat163Event5(event: Feat163AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat163Event6(event: Feat163AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat163Event7(event: Feat163AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat163Event8(event: Feat163AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat163Event9(event: Feat163AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat163Event10(event: Feat163AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat163Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat163Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat163(u: CoreUser): Feat163Projection1 =
    Feat163Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat163Projection1> {
    val list = java.util.ArrayList<Feat163Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat163(u)
    }
    return list
}
