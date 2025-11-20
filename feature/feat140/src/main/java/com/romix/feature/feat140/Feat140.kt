package com.romix.feature.feat140

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat140Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat140UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat140FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat140UserSummary
)

data class Feat140UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat140NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat140Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat140Config = Feat140Config()
) {

    fun loadSnapshot(userId: Long): Feat140NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat140NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat140UserSummary {
        return Feat140UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat140FeedItem> {
        val result = java.util.ArrayList<Feat140FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat140FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat140UiMapper {

    fun mapToUi(model: List<Feat140FeedItem>): Feat140UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat140UiModel(
            header = UiText("Feat140 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat140UiModel =
        Feat140UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat140UiModel =
        Feat140UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat140UiModel =
        Feat140UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat140Service(
    private val repository: Feat140Repository,
    private val uiMapper: Feat140UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat140UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat140UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat140UserItem1(val user: CoreUser, val label: String)
data class Feat140UserItem2(val user: CoreUser, val label: String)
data class Feat140UserItem3(val user: CoreUser, val label: String)
data class Feat140UserItem4(val user: CoreUser, val label: String)
data class Feat140UserItem5(val user: CoreUser, val label: String)
data class Feat140UserItem6(val user: CoreUser, val label: String)
data class Feat140UserItem7(val user: CoreUser, val label: String)
data class Feat140UserItem8(val user: CoreUser, val label: String)
data class Feat140UserItem9(val user: CoreUser, val label: String)
data class Feat140UserItem10(val user: CoreUser, val label: String)

data class Feat140StateBlock1(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock2(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock3(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock4(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock5(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock6(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock7(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock8(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock9(val state: Feat140UiModel, val checksum: Int)
data class Feat140StateBlock10(val state: Feat140UiModel, val checksum: Int)

fun buildFeat140UserItem(user: CoreUser, index: Int): Feat140UserItem1 {
    return Feat140UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat140StateBlock(model: Feat140UiModel): Feat140StateBlock1 {
    return Feat140StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat140UserSummary> {
    val list = java.util.ArrayList<Feat140UserSummary>(users.size)
    for (user in users) {
        list += Feat140UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat140UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat140UiModel {
    val summaries = (0 until count).map {
        Feat140UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat140UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat140UiModel> {
    val models = java.util.ArrayList<Feat140UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat140AnalyticsEvent1(val name: String, val value: String)
data class Feat140AnalyticsEvent2(val name: String, val value: String)
data class Feat140AnalyticsEvent3(val name: String, val value: String)
data class Feat140AnalyticsEvent4(val name: String, val value: String)
data class Feat140AnalyticsEvent5(val name: String, val value: String)
data class Feat140AnalyticsEvent6(val name: String, val value: String)
data class Feat140AnalyticsEvent7(val name: String, val value: String)
data class Feat140AnalyticsEvent8(val name: String, val value: String)
data class Feat140AnalyticsEvent9(val name: String, val value: String)
data class Feat140AnalyticsEvent10(val name: String, val value: String)

fun logFeat140Event1(event: Feat140AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat140Event2(event: Feat140AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat140Event3(event: Feat140AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat140Event4(event: Feat140AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat140Event5(event: Feat140AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat140Event6(event: Feat140AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat140Event7(event: Feat140AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat140Event8(event: Feat140AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat140Event9(event: Feat140AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat140Event10(event: Feat140AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat140Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat140Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat140(u: CoreUser): Feat140Projection1 =
    Feat140Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat140Projection1> {
    val list = java.util.ArrayList<Feat140Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat140(u)
    }
    return list
}
