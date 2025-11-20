package com.romix.feature.feat251

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat251Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat251UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat251FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat251UserSummary
)

data class Feat251UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat251NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat251Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat251Config = Feat251Config()
) {

    fun loadSnapshot(userId: Long): Feat251NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat251NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat251UserSummary {
        return Feat251UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat251FeedItem> {
        val result = java.util.ArrayList<Feat251FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat251FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat251UiMapper {

    fun mapToUi(model: List<Feat251FeedItem>): Feat251UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat251UiModel(
            header = UiText("Feat251 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat251UiModel =
        Feat251UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat251UiModel =
        Feat251UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat251UiModel =
        Feat251UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat251Service(
    private val repository: Feat251Repository,
    private val uiMapper: Feat251UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat251UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat251UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat251UserItem1(val user: CoreUser, val label: String)
data class Feat251UserItem2(val user: CoreUser, val label: String)
data class Feat251UserItem3(val user: CoreUser, val label: String)
data class Feat251UserItem4(val user: CoreUser, val label: String)
data class Feat251UserItem5(val user: CoreUser, val label: String)
data class Feat251UserItem6(val user: CoreUser, val label: String)
data class Feat251UserItem7(val user: CoreUser, val label: String)
data class Feat251UserItem8(val user: CoreUser, val label: String)
data class Feat251UserItem9(val user: CoreUser, val label: String)
data class Feat251UserItem10(val user: CoreUser, val label: String)

data class Feat251StateBlock1(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock2(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock3(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock4(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock5(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock6(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock7(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock8(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock9(val state: Feat251UiModel, val checksum: Int)
data class Feat251StateBlock10(val state: Feat251UiModel, val checksum: Int)

fun buildFeat251UserItem(user: CoreUser, index: Int): Feat251UserItem1 {
    return Feat251UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat251StateBlock(model: Feat251UiModel): Feat251StateBlock1 {
    return Feat251StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat251UserSummary> {
    val list = java.util.ArrayList<Feat251UserSummary>(users.size)
    for (user in users) {
        list += Feat251UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat251UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat251UiModel {
    val summaries = (0 until count).map {
        Feat251UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat251UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat251UiModel> {
    val models = java.util.ArrayList<Feat251UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat251AnalyticsEvent1(val name: String, val value: String)
data class Feat251AnalyticsEvent2(val name: String, val value: String)
data class Feat251AnalyticsEvent3(val name: String, val value: String)
data class Feat251AnalyticsEvent4(val name: String, val value: String)
data class Feat251AnalyticsEvent5(val name: String, val value: String)
data class Feat251AnalyticsEvent6(val name: String, val value: String)
data class Feat251AnalyticsEvent7(val name: String, val value: String)
data class Feat251AnalyticsEvent8(val name: String, val value: String)
data class Feat251AnalyticsEvent9(val name: String, val value: String)
data class Feat251AnalyticsEvent10(val name: String, val value: String)

fun logFeat251Event1(event: Feat251AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat251Event2(event: Feat251AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat251Event3(event: Feat251AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat251Event4(event: Feat251AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat251Event5(event: Feat251AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat251Event6(event: Feat251AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat251Event7(event: Feat251AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat251Event8(event: Feat251AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat251Event9(event: Feat251AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat251Event10(event: Feat251AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat251Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat251Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat251(u: CoreUser): Feat251Projection1 =
    Feat251Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat251Projection1> {
    val list = java.util.ArrayList<Feat251Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat251(u)
    }
    return list
}
