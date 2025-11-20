package com.romix.feature.feat342

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat342Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat342UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat342FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat342UserSummary
)

data class Feat342UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat342NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat342Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat342Config = Feat342Config()
) {

    fun loadSnapshot(userId: Long): Feat342NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat342NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat342UserSummary {
        return Feat342UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat342FeedItem> {
        val result = java.util.ArrayList<Feat342FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat342FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat342UiMapper {

    fun mapToUi(model: List<Feat342FeedItem>): Feat342UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat342UiModel(
            header = UiText("Feat342 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat342UiModel =
        Feat342UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat342UiModel =
        Feat342UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat342UiModel =
        Feat342UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat342Service(
    private val repository: Feat342Repository,
    private val uiMapper: Feat342UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat342UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat342UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat342UserItem1(val user: CoreUser, val label: String)
data class Feat342UserItem2(val user: CoreUser, val label: String)
data class Feat342UserItem3(val user: CoreUser, val label: String)
data class Feat342UserItem4(val user: CoreUser, val label: String)
data class Feat342UserItem5(val user: CoreUser, val label: String)
data class Feat342UserItem6(val user: CoreUser, val label: String)
data class Feat342UserItem7(val user: CoreUser, val label: String)
data class Feat342UserItem8(val user: CoreUser, val label: String)
data class Feat342UserItem9(val user: CoreUser, val label: String)
data class Feat342UserItem10(val user: CoreUser, val label: String)

data class Feat342StateBlock1(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock2(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock3(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock4(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock5(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock6(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock7(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock8(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock9(val state: Feat342UiModel, val checksum: Int)
data class Feat342StateBlock10(val state: Feat342UiModel, val checksum: Int)

fun buildFeat342UserItem(user: CoreUser, index: Int): Feat342UserItem1 {
    return Feat342UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat342StateBlock(model: Feat342UiModel): Feat342StateBlock1 {
    return Feat342StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat342UserSummary> {
    val list = java.util.ArrayList<Feat342UserSummary>(users.size)
    for (user in users) {
        list += Feat342UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat342UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat342UiModel {
    val summaries = (0 until count).map {
        Feat342UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat342UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat342UiModel> {
    val models = java.util.ArrayList<Feat342UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat342AnalyticsEvent1(val name: String, val value: String)
data class Feat342AnalyticsEvent2(val name: String, val value: String)
data class Feat342AnalyticsEvent3(val name: String, val value: String)
data class Feat342AnalyticsEvent4(val name: String, val value: String)
data class Feat342AnalyticsEvent5(val name: String, val value: String)
data class Feat342AnalyticsEvent6(val name: String, val value: String)
data class Feat342AnalyticsEvent7(val name: String, val value: String)
data class Feat342AnalyticsEvent8(val name: String, val value: String)
data class Feat342AnalyticsEvent9(val name: String, val value: String)
data class Feat342AnalyticsEvent10(val name: String, val value: String)

fun logFeat342Event1(event: Feat342AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat342Event2(event: Feat342AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat342Event3(event: Feat342AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat342Event4(event: Feat342AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat342Event5(event: Feat342AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat342Event6(event: Feat342AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat342Event7(event: Feat342AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat342Event8(event: Feat342AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat342Event9(event: Feat342AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat342Event10(event: Feat342AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat342Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat342Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat342(u: CoreUser): Feat342Projection1 =
    Feat342Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat342Projection1> {
    val list = java.util.ArrayList<Feat342Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat342(u)
    }
    return list
}
