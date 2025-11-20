package com.romix.feature.feat148

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat148Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat148UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat148FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat148UserSummary
)

data class Feat148UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat148NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat148Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat148Config = Feat148Config()
) {

    fun loadSnapshot(userId: Long): Feat148NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat148NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat148UserSummary {
        return Feat148UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat148FeedItem> {
        val result = java.util.ArrayList<Feat148FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat148FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat148UiMapper {

    fun mapToUi(model: List<Feat148FeedItem>): Feat148UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat148UiModel(
            header = UiText("Feat148 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat148UiModel =
        Feat148UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat148UiModel =
        Feat148UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat148UiModel =
        Feat148UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat148Service(
    private val repository: Feat148Repository,
    private val uiMapper: Feat148UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat148UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat148UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat148UserItem1(val user: CoreUser, val label: String)
data class Feat148UserItem2(val user: CoreUser, val label: String)
data class Feat148UserItem3(val user: CoreUser, val label: String)
data class Feat148UserItem4(val user: CoreUser, val label: String)
data class Feat148UserItem5(val user: CoreUser, val label: String)
data class Feat148UserItem6(val user: CoreUser, val label: String)
data class Feat148UserItem7(val user: CoreUser, val label: String)
data class Feat148UserItem8(val user: CoreUser, val label: String)
data class Feat148UserItem9(val user: CoreUser, val label: String)
data class Feat148UserItem10(val user: CoreUser, val label: String)

data class Feat148StateBlock1(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock2(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock3(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock4(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock5(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock6(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock7(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock8(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock9(val state: Feat148UiModel, val checksum: Int)
data class Feat148StateBlock10(val state: Feat148UiModel, val checksum: Int)

fun buildFeat148UserItem(user: CoreUser, index: Int): Feat148UserItem1 {
    return Feat148UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat148StateBlock(model: Feat148UiModel): Feat148StateBlock1 {
    return Feat148StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat148UserSummary> {
    val list = java.util.ArrayList<Feat148UserSummary>(users.size)
    for (user in users) {
        list += Feat148UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat148UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat148UiModel {
    val summaries = (0 until count).map {
        Feat148UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat148UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat148UiModel> {
    val models = java.util.ArrayList<Feat148UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat148AnalyticsEvent1(val name: String, val value: String)
data class Feat148AnalyticsEvent2(val name: String, val value: String)
data class Feat148AnalyticsEvent3(val name: String, val value: String)
data class Feat148AnalyticsEvent4(val name: String, val value: String)
data class Feat148AnalyticsEvent5(val name: String, val value: String)
data class Feat148AnalyticsEvent6(val name: String, val value: String)
data class Feat148AnalyticsEvent7(val name: String, val value: String)
data class Feat148AnalyticsEvent8(val name: String, val value: String)
data class Feat148AnalyticsEvent9(val name: String, val value: String)
data class Feat148AnalyticsEvent10(val name: String, val value: String)

fun logFeat148Event1(event: Feat148AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat148Event2(event: Feat148AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat148Event3(event: Feat148AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat148Event4(event: Feat148AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat148Event5(event: Feat148AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat148Event6(event: Feat148AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat148Event7(event: Feat148AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat148Event8(event: Feat148AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat148Event9(event: Feat148AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat148Event10(event: Feat148AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat148Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat148Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat148(u: CoreUser): Feat148Projection1 =
    Feat148Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat148Projection1> {
    val list = java.util.ArrayList<Feat148Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat148(u)
    }
    return list
}
